package io.scicast.streamesh.core.internal;

import io.scicast.streamesh.core.*;
import io.scicast.streamesh.core.exception.DuplicateDefinitionException;
import io.scicast.streamesh.core.exception.NotFoundException;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InMemoryStreameshOrchestrator implements StreameshOrchestrator {

    public static final String DUPLICATE_DEFINITION_MSG = "Definition with name %s already present. Please choose a different name.";
    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private OrchestrationDriver driver;
    private Map<String, CallableDefinition> definitions = new HashMap<>();
    private Map<String, CallableDefinition> definitionsByName = new HashMap<>();

    public InMemoryStreameshOrchestrator() {
        ServiceLoader<OrchestrationDriver> loader = ServiceLoader.load(OrchestrationDriver.class);

        this.driver = StreamSupport.stream(loader.spliterator(), false)
                .peek(impl -> logger.info(
                        "Found orchestration driver of type " + impl.getClass().getSimpleName()))
                .findFirst()
                .map(impl -> {
                    logger.info("Using orchestration driver " + impl.getClass().getSimpleName());
                    return impl;
                })
                .orElseThrow(() -> new RuntimeException("No orchestration driver. Booting sequence aborted."));
    }

    public String applyDefinition(CallableDefinition definition) {

        if(!definitionsByName.get(definition.getName()).equals(null)) {
            throw new DuplicateDefinitionException(String.format(DUPLICATE_DEFINITION_MSG, definition.getName()));
        }

        String imageId = driver.retrieveContainerImage(definition.getImage());
        String definitionId = UUID.randomUUID().toString();

        CallableDefinition callableDefinition = definition.withImageId(imageId)
                .withId(definitionId);
        definitions.put(definitionId, callableDefinition);
        definitionsByName.put(definition.getName(),callableDefinition);
        return definitionId;
    }

    public CallableDefinition getDefinition(String id) {
        CallableDefinition definition = definitions.get(id);
        if(definition == null) {
            throw new NotFoundException(String.format("No definition with id %s found", id));
        }
        return definition;
    }

    public void removeDefinition(String id) {
        CallableDefinition removed = definitions.remove(id);
        if(removed != null) {
            definitionsByName.remove(removed.getName());
        }
    }

    public Set<CallableDefinition> getDefinitions() {
        return definitions.entrySet().stream()
                .map(e -> e.getValue())
                .collect(Collectors.toSet());
    }

    public Set<JobDescriptor> getAllJobs() {
        return null;
    }

    public Set<JobDescriptor> getJobsByDefinition(String definitionId) {
        return null;
    }

    public JobDescriptor scheduleJob(String definitionId, JobInput input) {
        return null;
    }

    public OutputHandle getJobOutput(String jobDescriptorId) {
        return null;
    }
}
