/**
 * Business Service Layer.
 *
 * <ul>
 *   <li>One service class per domain aggregate (e.g. OrderService, CustomerService)</li>
 *   <li>{@code CamundaService} — wraps all Camunda 8.9 client calls:
 *       <ul>
 *         <li>deploy()              — POST /v2/deployments</li>
 *         <li>startProcess()        — POST /v2/process-instances</li>
 *         <li>getActiveInstances()  — POST /v2/process-instances/search</li>
 *         <li>getUserTasks()        — POST /v2/user-tasks/search</li>
 *         <li>completeTask()        — via newCompleteCommand(taskKey)</li>
 *         <li>getIncidents()        — POST /v2/incidents/search</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p>Services are @Transactional where they write to the database.</p>
 */
package com.aaseya.momthathel.service;
