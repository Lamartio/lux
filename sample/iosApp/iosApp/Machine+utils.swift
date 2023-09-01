import Combine
import shared
import SwiftUI


typealias AppMachine = Machine<AppState, AppActions>

extension Machine where Output == AppState, Actions == AppActions{
    convenience init(state: AppState = AppState.companion.initial)  {
        let subject = CurrentValueSubject<AppState, Never>(state)
        let actions = AppActions(get: { subject.value }, set: subject.send)
        
        self.init(
            publisher: subject.eraseToAnyPublisher(),
            getState: { subject.value },
            actions: actions
        )
    }
}
