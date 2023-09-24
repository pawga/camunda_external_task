/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.demo

import org.camunda.bpm.client.spring.event.SubscriptionInitializedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class Subscriptions : ApplicationListener<SubscriptionInitializedEvent> {

    override fun onApplicationEvent(event: SubscriptionInitializedEvent) {
        val springTopicSubscription = event.source
        val topicName = springTopicSubscription.topicName
        logger?.info("Subscription with topic name '{}' initialized", topicName)
        if (!springTopicSubscription.isOpen) {
            logger?.info("Subscription with topic name '{}' not yet opened", topicName)

            // do something before subscription is opened
            springTopicSubscription.open()
            logger?.info("Subscription with topic name '{}' opened", topicName)
            springTopicSubscription.close()
            logger?.info("Subscription with topic name '{}' temporarily closed", topicName)

            // do something with subscription closed
            springTopicSubscription.open()
            logger?.info("Subscription with topic name '{}' reopened again", topicName)
        }
    }

    companion object {
        protected val logger: Logger? = LoggerFactory.getLogger(Subscriptions::class.java)
    }
}
