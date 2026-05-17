/**
 * REST Controllers.
 *
 * <p>Each controller maps a logical domain resource to HTTP endpoints.</p>
 * <ul>
 *   <li>ProcessController  — deploy BPMN, list process instances, incidents</li>
 *   <li>Domain controllers — submit entities, track status, trigger actions</li>
 * </ul>
 *
 * <p>All controllers consume/produce {@code application/json}.</p>
 * <p>Responses are wrapped in a generic {@code ApiResponse<T>} envelope.</p>
 */
package com.aaseya.momthathel.controller;
