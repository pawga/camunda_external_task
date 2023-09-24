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

import org.camunda.bpm.client.interceptor.ClientRequestContext
import org.camunda.bpm.client.interceptor.ClientRequestInterceptor
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription
import org.camunda.bpm.client.spring.boot.starter.ClientProperties
import org.camunda.bpm.client.task.ExternalTask
import org.camunda.bpm.client.task.ExternalTaskHandler
import org.camunda.bpm.client.task.ExternalTaskService
import org.camunda.bpm.client.variable.ClientValues
import org.camunda.bpm.engine.variable.value.TypedValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class HandlerConfiguration(properties: ClientProperties) {

    protected val workerId: String = properties.workerId

    @Bean
    @ExternalTaskSubscription("invoiceCreator")
    fun invoiceCreatorHandler(): ExternalTaskHandler {
        return ExternalTaskHandler { externalTask: ExternalTask, externalTaskService: ExternalTaskService ->

            // instantiate an invoice object
            val invoice = Invoice("ABC-"+ Random().nextInt(16))

            // create an object typed variable with the serialization format XML
            val invoiceValue = ClientValues
                .objectValue(invoice)
                .serializationDataFormat("application/json")
                .create()

            // add the invoice object and its id to a map
            val variables: MutableMap<String, Any> = HashMap()
            variables["invoiceId"] = invoice.id ?: "empty"
            variables["invoice"] = invoiceValue

            // select the scope of the variables
            val isRandomSample = Math.random() <= 0.5
            if (isRandomSample) {
                externalTaskService.complete(externalTask, variables)
            } else {
                externalTaskService.complete(externalTask, null, variables)
            }
            logger?.info("{}: invoiceCreator The External Task {} has been completed!",  workerId, externalTask.id)
        }
    }

    @Bean
    @ExternalTaskSubscription(topicName = "invoiceArchiver", autoOpen = false)
    fun invoiceArchiverHandler(): ExternalTaskHandler {
        return ExternalTaskHandler { externalTask: ExternalTask, externalTaskService: ExternalTaskService ->
            val typedInvoice = externalTask.getVariableTyped<TypedValue>("invoice")
            val invoice = typedInvoice.value as Invoice
            logger?.info("{}: invoiceArchiver Invoice on process scope archived: {}",
                workerId,
                invoice)
            externalTaskService.complete(externalTask)
        }
    }

    @ExternalTaskSubscription("requestRejecter")
    @Bean
    fun requestRejecter(): ExternalTaskHandler {
        return ExternalTaskHandler { externalTask: ExternalTask, externalTaskService: ExternalTaskService ->
            val score = externalTask.getVariable<Int>("score")
            externalTaskService.complete(externalTask)
            logger?.info(
                "{}: The External Task {} has been rejected with score {}!",
                workerId,
                externalTask.id,
                score
            )
        }
    }

    @Bean
    fun interceptor(): ClientRequestInterceptor {
        return ClientRequestInterceptor { context: ClientRequestContext ->
            logger?.info("Request interceptor called!")
            context.addHeader("X-MY-HEADER", "External Tasks Rock!")
        }
    }

    companion object {
        protected val logger: Logger? = LoggerFactory.getLogger(HandlerConfiguration::class.java)
    }
}
