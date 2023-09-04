import Combine
import shared

struct MachinePublisher<Output: AnyObject, Actions: AnyObject>: Publisher {
    typealias Failure = Never
    
    let machine: LuxMachine<Output, Actions>
    
    func receive<S>(subscriber: S) where S : Subscriber, Never == S.Failure, Output == S.Input {
        let cancel = machine.collect(
            onEach: { input in subscriber.receive(input) },
            onCompletion: { subscriber.receive(completion: .finished) }
        )
        let cancellable = AnyCancellable(cancel)
        let subscription = JobSubscription(cancellable)
        
        subscriber.receive(subscription: subscription)
    }
    
    private class JobSubscription: Subscription {
        
        private let cancellable: Cancellable
        
        init(_ cancellable: Cancellable) {
            self.cancellable = cancellable
        }
        
        func request(_ demand: Subscribers.Demand) {
        }
        
        func cancel() {
            cancellable.cancel()
        }
    }
}
