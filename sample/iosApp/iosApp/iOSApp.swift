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
