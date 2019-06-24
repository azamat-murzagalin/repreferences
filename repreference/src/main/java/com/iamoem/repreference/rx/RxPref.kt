package com.iamoem.repreference.rx

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Maybe
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class RxPref {

    private val emitters: MutableMap<String, EmittersContainer> = HashMap()

    fun <T> createFlowable(key: String, callable : () -> T?) : Flowable<T> {
        val maybe = Maybe.fromCallable(callable)
        val scheduler = Schedulers.io()
        return Flowable.create<Any>(
            { emitter ->
                if (!emitter.isCancelled) {
                    addEmitterHolder(key, emitter)
                    emitter.setDisposable(Disposables.fromRunnable {
                        removeEmitterHolder(key, emitter)
                    })
                }

                if (!emitter.isCancelled) {
                    emitter.onNext(Any())
                }
            },
            BackpressureStrategy.LATEST)
            .subscribeOn(scheduler)
            .unsubscribeOn(scheduler)
            .observeOn(scheduler)
            .flatMapMaybe {
                return@flatMapMaybe maybe
            }
    }

    fun notifyValueChanged(key: String) {
        emitters[key]?.notifyEmitters()
    }

    private fun removeEmitterHolder(key: String, emitter: FlowableEmitter<Any>) {
        synchronized(emitters) {
            emitters[key]?.removeEmitter(emitter)
        }
    }

    private fun addEmitterHolder(key: String, emitter: FlowableEmitter<Any>) {
        synchronized(emitters) {
            if (emitters.containsKey(key)) {
                emitters[key]!!.addEmitter(emitter)
            } else {
                val emittersContainer = EmittersContainer().apply {
                    addEmitter(emitter)
                }
                emitters[key] = emittersContainer
            }
        }
    }

    fun notifyAllChanged() {
        emitters.forEach {
            it.value.notifyEmitters()
        }
    }
}

class EmittersContainer {
    private val list : MutableList<FlowableEmitter<Any>> = ArrayList()

    fun notifyEmitters() = list.forEach {
        if (!it.isCancelled) {
            it.onNext(Any())
        }
    }

    fun addEmitter(emitterHolder: FlowableEmitter<Any>) {
        list.add(emitterHolder)
    }

    fun removeEmitter(emitter: FlowableEmitter<Any>) {
        list.remove(emitter)
    }
}
