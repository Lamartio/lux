#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class OpticsCallback<T>, OpticsOptionCompanion, OpticsOption<T>, OpticsOptionSome<T>, OpticsTestA, OpticsTestB<T>, OpticsTestC<T>;

@protocol OpticsTest, OpticsActions, OpticsNetwork;

NS_ASSUME_NONNULL_BEGIN
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunknown-warning-option"
#pragma clang diagnostic ignored "-Wincompatible-property-type"
#pragma clang diagnostic ignored "-Wnullability"

#pragma push_macro("_Nullable_result")
#if !__has_feature(nullability_nullable_result)
#undef _Nullable_result
#define _Nullable_result _Nullable
#endif

__attribute__((swift_name("KotlinBase")))
@interface OpticsBase : NSObject
- (instancetype)init __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
+ (void)initialize __attribute__((objc_requires_super));
@end;

@interface OpticsBase (OpticsBaseCopying) <NSCopying>
@end;

__attribute__((swift_name("KotlinMutableSet")))
@interface OpticsMutableSet<ObjectType> : NSMutableSet<ObjectType>
@end;

__attribute__((swift_name("KotlinMutableDictionary")))
@interface OpticsMutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end;

@interface NSError (NSErrorOpticsKotlinException)
@property (readonly) id _Nullable kotlinException;
@end;

__attribute__((swift_name("KotlinNumber")))
@interface OpticsNumber : NSNumber
- (instancetype)initWithChar:(char)value __attribute__((unavailable));
- (instancetype)initWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
- (instancetype)initWithShort:(short)value __attribute__((unavailable));
- (instancetype)initWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
- (instancetype)initWithInt:(int)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
- (instancetype)initWithLong:(long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
- (instancetype)initWithLongLong:(long long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
- (instancetype)initWithFloat:(float)value __attribute__((unavailable));
- (instancetype)initWithDouble:(double)value __attribute__((unavailable));
- (instancetype)initWithBool:(BOOL)value __attribute__((unavailable));
- (instancetype)initWithInteger:(NSInteger)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
+ (instancetype)numberWithChar:(char)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
+ (instancetype)numberWithShort:(short)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
+ (instancetype)numberWithInt:(int)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
+ (instancetype)numberWithLong:(long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
+ (instancetype)numberWithLongLong:(long long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
+ (instancetype)numberWithFloat:(float)value __attribute__((unavailable));
+ (instancetype)numberWithDouble:(double)value __attribute__((unavailable));
+ (instancetype)numberWithBool:(BOOL)value __attribute__((unavailable));
+ (instancetype)numberWithInteger:(NSInteger)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
@end;

__attribute__((swift_name("KotlinByte")))
@interface OpticsByte : OpticsNumber
- (instancetype)initWithChar:(char)value;
+ (instancetype)numberWithChar:(char)value;
@end;

__attribute__((swift_name("KotlinUByte")))
@interface OpticsUByte : OpticsNumber
- (instancetype)initWithUnsignedChar:(unsigned char)value;
+ (instancetype)numberWithUnsignedChar:(unsigned char)value;
@end;

__attribute__((swift_name("KotlinShort")))
@interface OpticsShort : OpticsNumber
- (instancetype)initWithShort:(short)value;
+ (instancetype)numberWithShort:(short)value;
@end;

__attribute__((swift_name("KotlinUShort")))
@interface OpticsUShort : OpticsNumber
- (instancetype)initWithUnsignedShort:(unsigned short)value;
+ (instancetype)numberWithUnsignedShort:(unsigned short)value;
@end;

__attribute__((swift_name("KotlinInt")))
@interface OpticsInt : OpticsNumber
- (instancetype)initWithInt:(int)value;
+ (instancetype)numberWithInt:(int)value;
@end;

__attribute__((swift_name("KotlinUInt")))
@interface OpticsUInt : OpticsNumber
- (instancetype)initWithUnsignedInt:(unsigned int)value;
+ (instancetype)numberWithUnsignedInt:(unsigned int)value;
@end;

__attribute__((swift_name("KotlinLong")))
@interface OpticsLong : OpticsNumber
- (instancetype)initWithLongLong:(long long)value;
+ (instancetype)numberWithLongLong:(long long)value;
@end;

__attribute__((swift_name("KotlinULong")))
@interface OpticsULong : OpticsNumber
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value;
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value;
@end;

__attribute__((swift_name("KotlinFloat")))
@interface OpticsFloat : OpticsNumber
- (instancetype)initWithFloat:(float)value;
+ (instancetype)numberWithFloat:(float)value;
@end;

__attribute__((swift_name("KotlinDouble")))
@interface OpticsDouble : OpticsNumber
- (instancetype)initWithDouble:(double)value;
+ (instancetype)numberWithDouble:(double)value;
@end;

__attribute__((swift_name("KotlinBoolean")))
@interface OpticsBoolean : OpticsNumber
- (instancetype)initWithBool:(BOOL)value;
+ (instancetype)numberWithBool:(BOOL)value;
@end;

__attribute__((swift_name("Actions")))
@protocol OpticsActions
@required
- (void)decrement __attribute__((swift_name("decrement()")));
- (void)increment __attribute__((swift_name("increment()")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Callback")))
@interface OpticsCallback<T> : OpticsBase
- (instancetype)initWithOnSuccess:(void (^)(T))onSuccess onFailure:(void (^)(void))onFailure __attribute__((swift_name("init(onSuccess:onFailure:)"))) __attribute__((objc_designated_initializer));
@property (readonly) void (^onFailure)(void) __attribute__((swift_name("onFailure")));
@property (readonly) void (^onSuccess)(T) __attribute__((swift_name("onSuccess")));
@end;

__attribute__((swift_name("Network")))
@protocol OpticsNetwork
@required
- (NSString *)ching __attribute__((swift_name("ching()")));
- (OpticsCallback<NSString *> *)chong __attribute__((swift_name("chong()")));
@end;

__attribute__((swift_name("Option")))
@interface OpticsOption<T> : OpticsBase
@property (class, readonly, getter=companion) OpticsOptionCompanion *companion __attribute__((swift_name("companion")));
- (OpticsOption<id> *)flatMapTransform:(OpticsOption<id> *(^)(T _Nullable))transform __attribute__((swift_name("flatMap(transform:)")));
- (id)foldIfNone:(id (^)(void))ifNone ifSome:(id (^)(T _Nullable))ifSome __attribute__((swift_name("fold(ifNone:ifSome:)")));
- (T _Nullable)getOr:(T _Nullable)or_ __attribute__((swift_name("get(or:)")));
- (OpticsOption<id> *)mapTransform:(id _Nullable (^)(T _Nullable))transform __attribute__((swift_name("map(transform:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("OptionCompanion")))
@interface OpticsOptionCompanion : OpticsBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) OpticsOptionCompanion *shared __attribute__((swift_name("shared")));
- (OpticsOption<id> *)ofValue:(id _Nullable)value __attribute__((swift_name("of(value:)")));
- (OpticsOption<id> *)ofNone __attribute__((swift_name("ofNone()")));
- (OpticsOption<id> *)ofNullableValue:(id _Nullable)value __attribute__((swift_name("ofNullable(value:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("OptionNone")))
@interface OpticsOptionNone<T> : OpticsOption<T>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("OptionSome")))
@interface OpticsOptionSome<T> : OpticsOption<T>
- (instancetype)initWithValue:(T _Nullable)value __attribute__((swift_name("init(value:)"))) __attribute__((objc_designated_initializer));
- (T _Nullable)component1 __attribute__((swift_name("component1()"))) __attribute__((deprecated("use corresponding property instead")));
- (OpticsOptionSome<T> *)doCopyValue:(T _Nullable)value __attribute__((swift_name("doCopy(value:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) T _Nullable value __attribute__((swift_name("value")));
@end;

__attribute__((swift_name("Test")))
@protocol OpticsTest
@required
@property (readonly) id _Nullable value __attribute__((swift_name("value")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("TestA")))
@interface OpticsTestA : OpticsBase <OpticsTest>
- (instancetype)initWithValue:(NSString *)value __attribute__((swift_name("init(value:)"))) __attribute__((objc_designated_initializer));
- (NSString *)component1 __attribute__((swift_name("component1()"))) __attribute__((deprecated("use corresponding property instead")));
- (OpticsTestA *)doCopyValue:(NSString *)value __attribute__((swift_name("doCopy(value:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("TestB")))
@interface OpticsTestB<T> : OpticsBase <OpticsTest>
- (instancetype)initWithValue:(T _Nullable)value __attribute__((swift_name("init(value:)"))) __attribute__((objc_designated_initializer));
- (T _Nullable)component1 __attribute__((swift_name("component1()"))) __attribute__((deprecated("use corresponding property instead")));
- (OpticsTestB<T> *)doCopyValue:(T _Nullable)value __attribute__((swift_name("doCopy(value:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) T _Nullable value __attribute__((swift_name("value")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("TestC")))
@interface OpticsTestC<T> : OpticsBase <OpticsTest>
- (instancetype)initWithValue:(T)value __attribute__((swift_name("init(value:)"))) __attribute__((objc_designated_initializer));
- (T)component1 __attribute__((swift_name("component1()"))) __attribute__((deprecated("use corresponding property instead")));
- (OpticsTestC<T> *)doCopyValue:(T)value __attribute__((swift_name("doCopy(value:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) T value __attribute__((swift_name("value")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("OptionKt")))
@interface OpticsOptionKt : OpticsBase
+ (id<OpticsActions>)actionsOfNetwork:(id<OpticsNetwork>)network __attribute__((swift_name("actionsOf(network:)")));
+ (OpticsOption<id> *)optionOfValue:(id _Nullable)value __attribute__((swift_name("optionOf(value:)")));
+ (OpticsOption<id> *)optionOfNone __attribute__((swift_name("optionOfNone()")));
+ (OpticsOption<id> *)optionOfNullableValue:(id _Nullable)value __attribute__((swift_name("optionOfNullable(value:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("UtilsKt")))
@interface OpticsUtilsKt : OpticsBase
+ (id _Nullable)identityValue:(id _Nullable)value __attribute__((swift_name("identity(value:)")));
@end;

#pragma pop_macro("_Nullable_result")
#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END
