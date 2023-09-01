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

struct CounterView: View {
    
    @ObservedObject var counter: Machine<Int32, CounterActions>
    @State var text: String = ""
    
    init(counter: Machine<Int32, CounterActions>) {
        self.counter = counter
    }
    
    var body: some View {
        VStack(alignment: .center) {
            Label("Counter", systemImage: "number")
                .labelStyle(.titleOnly)
            
            TextField("Number", text: $text)
                .frame(maxWidth: 120)
                .textFieldStyle(.roundedBorder)
                .disabled(true)
            
            HStack {
                Button("-", action: counter.actions.decrement)
                    .buttonStyle(.borderedProminent)
                Button("+", action: counter.actions.increment)
                    .buttonStyle(.borderedProminent)
            }
        }
        .onReceive(counter.$state, perform: { count in text = String(count) })
    }
}
