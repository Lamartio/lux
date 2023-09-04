import SwiftUI
import shared
import Combine

@main
struct iOSApp: App {
    let machine = AppMachine()
    
	var body: some Scene {
		WindowGroup {
            CounterView(counter: machine.counter)
            Divider().padding()
            ClockView(clock: machine.clock)
		}
	}
}


func use<T, R>(value: T, _ block: (T) -> R) -> R {
    return block(value)
}

func fold<I, O, R>(
    ifIdle: @escaping () -> R,
    ifExecuting: @escaping  (I) -> R,
    ifFailure: @escaping (KotlinThrowable) -> R,
    ifSuccess: @escaping (O) -> R
) -> (LuxAsync<I,O>) -> R {
    return { a in
        if let executing = a.asExecuting() {
            return ifExecuting(executing.input)
        } else if let failure = a.asFailure() {
            return ifFailure(failure.reason)
        } else if let success = a.asSuccess() {
            return ifSuccess(success.result)
        } else {
           return ifIdle()
        }
    }
}
