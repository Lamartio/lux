import SwiftUI
import shared
import Combine

struct CounterView: View {
    
    var counter: LuxMachine<KotlinInt, CounterActions>
    @State private var text: String = ""
    
    init(counter: LuxMachine<KotlinInt, CounterActions>) {
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
        .onReceive(MachinePublisher(machine: counter), perform: { count in text = count.stringValue })
    }
}


