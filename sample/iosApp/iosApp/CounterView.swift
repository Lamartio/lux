import SwiftUI
import shared


struct CounterView: View {
    
    @ObservedObject var counter: Machine<Int32, CounterActions>
    @State private var text: String = ""
    
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
