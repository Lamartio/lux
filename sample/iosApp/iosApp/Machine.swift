import Combine
import SwiftUI

class Machine<Output, Actions>: ObservableObject {
    typealias Failure = Never
    
    let publisher: AnyPublisher<Output, Never>
    let getState: () -> Output
    @Published var state: Output
    let actions: Actions
    
    private var cancellables = Set<AnyCancellable>()
    
    init(publisher: AnyPublisher<Output, Failure>, getState: @escaping () -> Output, actions: Actions) {
        self.publisher = publisher
        self.getState = getState
        self.actions = actions
        self.state = getState()
        
        publisher
            .assign(to: \.state, on: self)
            .store(in: &cancellables)
    }
    
    func compose<O: Equatable>(state transform: @escaping (Output) -> O) -> Machine<O, Actions> {
        return Machine<O, Actions>(
            publisher: publisher.map(transform).eraseToAnyPublisher(),
            getState: { transform(self.getState()) },
            actions: actions
        )
    }
    
    func compose<A>(actions transform: (Actions) -> A) -> Machine<Output, A> {
        return Machine<Output, A>(
            publisher: publisher,
            getState: getState,
            actions: transform(actions)
        )
    }
    
    func compose<O: Equatable, A>(state: @escaping (Output) -> O, actions: (Actions) -> A) -> Machine<O, A> {
        return Machine<O, A>(
            publisher: publisher.map(state).eraseToAnyPublisher(),
            getState: { state(self.getState()) },
            actions: actions(self.actions)
        )
    }
    
    func compose<O: Equatable, A>(_ tuple: (state: (Output) -> O, actions: (Actions) -> A)) -> Machine<O, A> {
        let (state, actions) = tuple
        return compose(state: state, actions: actions)
    }
}
