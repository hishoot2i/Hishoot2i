package org.illegaller.ratabb.hishoot2i.data.rx

import io.reactivex.Scheduler

interface SchedulerProvider {
    fun ui(): Scheduler
    fun computation(): Scheduler
    fun newThread(): Scheduler
    fun io(): Scheduler
}
