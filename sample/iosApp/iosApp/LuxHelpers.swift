import SwiftUI
import Combine
import shared

extension KotlinThrowable: Error {
}

struct CollectiblePublisher<Output: AnyObject>: Publisher {
    typealias Failure = KotlinThrowable
    let collectible: LuxCollectible<Output>
    
    init(_ collectible: LuxCollectible<Output>) {
        self.collectible = collectible
    }
    
    func receive<S>(subscriber: S) where S : Subscriber, Failure == S.Failure, Output == S.Input {
        let cancel = collect(
            onEach: { _ = subscriber.receive($0) },
            onCompletion: {
                if let error = $0 {
                    subscriber.receive(completion: .failure(error))
                } else {
                    subscriber.receive(completion: .finished)
                }
            }
        )
        let anyCancellable = AnyCancellable(cancel)
        let subscription = SubscriptionWrapper(anyCancellable)

        subscriber.receive(subscription: subscription)
    }
    
    private func collect(onEach: @escaping (Output) -> Void, onCompletion: @escaping (KotlinThrowable?) -> Void) -> () -> Void {
        return collectible.collect(
            onEach: onEach,
            onCompletion: onCompletion
        )
    }
    
    private class SubscriptionWrapper: Subscription {
        
        private var cancellable: AnyCancellable?
    
        init(_ cancellable: AnyCancellable) {
            self.cancellable = cancellable
        }
        
        func cancel() {
            cancellable = nil
        }
        
        func request(_ demand: Subscribers.Demand) {
        }
    }
}

extension Publisher {
    func ignoreError() -> Publishers.Catch<Self, Empty<Output, Never>> {
        return self.catch({ _ in return Empty<Output, Never>() })
    }
}

func values<S, A>(from machine: LuxMachine<S, A>) -> AnyPublisher<S, Never>  {
    return values(from: machine, \.self)
}

func values<S, A, T>(from machine: LuxMachine<S, A>, _ keyPath: KeyPath<S,T>)  -> AnyPublisher<T, Never>  {
    return CollectiblePublisher(machine.collectible)
        .ignoreError()
        .map(keyPath)
        .eraseToAnyPublisher()
}
