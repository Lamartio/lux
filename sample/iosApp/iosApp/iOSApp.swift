import SwiftUI
import shared
import Combine


@main
struct iOSApp: App {
    let machine = AppMachine()
    
	var body: some Scene {
		WindowGroup {
            CounterView(counter: machine.compose(state: \.count, actions: \.counter))
		}
	}
}
