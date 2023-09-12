import SwiftUI
import shared

// A preview for starting and stopping an asynchronous function
struct ClockView: View {
    
    var clock: LuxMachine<KotlinInt, ClockActions>
    @State private var text: String = ""
    
    var body: some View {
        VStack(alignment: .center) {
            Label("Clock", systemImage: "clock")
                .labelStyle(.titleOnly)
            
            TextField("Number", text: $text)
                .frame(maxWidth: 120)
                .textFieldStyle(.roundedBorder)
                .disabled(true)
            
            HStack {
                Button("start", action: clock.actions.start)
                    .buttonStyle(.borderedProminent)
                Button("stop", action: clock.actions.stop)
                    .buttonStyle(.borderedProminent)
            }
        }
        .onReceive(publisher(of: clock).map(\.stringValue), perform: { text = $0 })
    }
}
