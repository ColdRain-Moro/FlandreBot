package kim.bifrost.rain.flandre.lib

import java.util.concurrent.CancellationException

/**
 * kim.bifrost.rain.flandre.lib.TryRun
 * Flandre
 *
 * @author 寒雨
 * @since 2022/6/23 16:24
 */
@JvmInline
value class TryRunResult(val throwable: Throwable?)

inline fun tryRun(block: () -> Unit): TryRunResult {
    return try {
        block()
        TryRunResult(null)
    } catch (e: Throwable) {
        TryRunResult(e)
    }
}

inline infix fun <reified T : Throwable> TryRunResult.catch(block: (t: T) -> Unit) {
    if (throwable is CancellationException) throw throwable
    if (throwable is T) {
        block(throwable)
    } else if (throwable != null) {
        throw throwable
    }
}

inline infix fun TryRunResult.catchAll(block: (t: Throwable) -> Unit) {
    if (throwable is CancellationException) throw throwable
    if (throwable != null) block(throwable)
}

inline infix fun <reified T : Throwable> TryRunResult.catching(block: (t: T) -> Unit): TryRunResult {
    return if (throwable is T) {
        block(throwable)
        TryRunResult(null)
    } else {
        this
    }
}

inline infix fun TryRunResult.finally(block: () -> Unit) {
    block()
    if (throwable != null) throw throwable
}