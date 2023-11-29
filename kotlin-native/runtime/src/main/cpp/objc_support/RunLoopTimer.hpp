/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#if KONAN_HAS_FOUNDATION_FRAMEWORK

#include <chrono>
#include <CoreFoundation/CoreFoundation.h>
#include <CoreFoundation/CFRunLoop.h>

#include "Utils.hpp"

namespace kotlin::objc_support {

// Smart pointer around `CFRunLoopTimer`.
//
// To attach to the current run loop use `attachToCurrentRunLoop`, it'll be detached when the returned `Subscription`
// is destroyed.
// `RunLoopTimer` is initialized with:
// - `std::function<void()>`, which will be called every time the run loop processes this timer.
// - `interval`, which is the desired average interval between firings
// - `initialFiring`, which is the minimum time before the first timer can fire.
// To override when the next firing can be performed, use `setNextFiring`.
// The underlying raw pointer is available via `handle`.
class RunLoopTimer : private Pinned {
public:
    // A token that `RunLoopTimer` is attached to a run loop.
    //
    // Must be destroyed on the same thread that called `attachToCurrentRunLoop`.
    class [[nodiscard]] Subscription : private Pinned {
    public:
        ~Subscription() {
            RuntimeAssert(runLoop_ == CFRunLoopGetCurrent(), "Must be destroyed on the same thread as created");
            CFRunLoopRemoveTimer(runLoop_, timer_, mode_);
        }

    private:
        friend class RunLoopTimer;

        Subscription(CFRunLoopTimerRef timer, CFRunLoopMode mode) noexcept : timer_(timer), runLoop_(CFRunLoopGetCurrent()), mode_(mode) {
            CFRunLoopAddTimer(runLoop_, timer, mode);
        }

        CFRunLoopTimerRef timer_;
        CFRunLoopRef runLoop_;
        CFRunLoopMode mode_;
    };

    // Create `RunLoopSource` with `callback` that will be invoked each time, when an attached run loop processes this timer.
    // `interval` sets the desired time between timer firing (the system will try to make the average time between firings be at least
    // `interval`, but the time between 2 consecutive tasks may be smaller if the current average is larger).
    // `initialFiring` sets the minimum time before the timer can be fired for the first time.
    RunLoopTimer(
            std::function<void()> callback, std::chrono::duration<double> interval, std::chrono::duration<double> initialFiring) noexcept :
        callback_(std::move(callback)),
        timerContext_{0, &callback_, nullptr, nullptr, nullptr},
        timer_(CFRunLoopTimerCreate(
                nullptr, CFAbsoluteTimeGetCurrent() + initialFiring.count(), interval.count(), 0, 0, &perform, &timerContext_)) {}

    ~RunLoopTimer() { CFRelease(timer_); }

    // Raw pointer to `CFRunLoopTimer`.
    CFRunLoopTimerRef handle() noexcept { return timer_; }

    // `interval` overrides the minimum time before the next timer firing. The override is for the next firing
    // only, after it, the initially supplied `interval` will be used again.
    void setNextFiring(std::chrono::duration<double> interval) noexcept {
        CFRunLoopTimerSetNextFireDate(timer_, CFAbsoluteTimeGetCurrent() + interval.count());
    }

    // Attach this `RunLoopTimer` to the current thread's run loop.
    Subscription attachToCurrentRunLoop(CFRunLoopMode mode = kCFRunLoopDefaultMode) noexcept { return Subscription(timer_, mode); }

private:
    static void perform(CFRunLoopTimerRef, void* callback) noexcept { static_cast<decltype(callback_)*>(callback)->operator()(); }

    std::function<void()> callback_; // TODO: std::function_ref?
    CFRunLoopTimerContext timerContext_;
    CFRunLoopTimerRef timer_;
};

} // namespace kotlin::objc_support

#endif