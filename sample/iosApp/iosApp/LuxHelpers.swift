import SwiftUI
import Combine
import shared

func publisher<S, A>(of machine: LuxMachine<S, A>) -> AnyPublisher<S, Never> {
    let publisher = Deferred(createPublisher: {
        let subject = PassthroughSubject<S, Never>()
        let cancel = machine.collect(
            onEach: subject.send,
            onCompletion: { _ in subject.send(completion: .finished) }
        )

        return subject.handleEvents(receiveCancel: cancel)
    })

    return publisher.eraseToAnyPublisher()
}
