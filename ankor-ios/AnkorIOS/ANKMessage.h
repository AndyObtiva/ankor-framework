//
//  ANKMessage.h
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ANKMessage : NSObject

@property NSString *property;
@property NSArray *stateProps;
@property NSDictionary *stateValues;

- (id) initWith: (NSString*)property stateProps:(NSArray*)stateProps stateValues:(NSDictionary*) stateValues;

@end
