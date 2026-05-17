/**
 * Camunda 8.9 Job Worker handlers.
 *
 * <p>Each class in this package handles one or more BPMN Service Task job types.
 * Workers are registered automatically by the Camunda Spring Boot Starter
 * when the application starts — no additional configuration required.</p>
 *
 * <p>Pattern:</p>
 * <pre>{@code
 * @Component
 * public class MyWorker {
 *
 *     @JobWorker(type = "my-job-type")          // matches BPMN task type
 *     public Map<String, Object> handle(
 *             @Variable String someVar,          // injected from process variables
 *             @Variable double anotherVar) {
 *         // ... business logic ...
 *         return Map.of("outputVar", result);    // returned vars → process instance
 *     }
 * }
 * }</pre>
 *
 * <p>The Camunda client auto-polls Zeebe for jobs of the configured type,
 * executes this method, and completes (or fails) the job automatically.</p>
 *
 * <p>Important Camunda 8.9 annotations:</p>
 * <ul>
 *   <li>{@code @JobWorker}  — {@code io.camunda.client.annotation.JobWorker}</li>
 *   <li>{@code @Variable}   — {@code io.camunda.client.annotation.Variable}</li>
 * </ul>
 */
package com.aaseya.momthathel.worker;
