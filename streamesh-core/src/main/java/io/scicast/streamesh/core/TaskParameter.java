package io.scicast.streamesh.core;

import io.scicast.streamesh.core.reflect.LocallyScoped;
import lombok.*;

@Builder
@Getter
@With
@NoArgsConstructor
@AllArgsConstructor
@LocallyScoped(using = "name")
public class TaskParameter {

    private String name;
    private String internalName;
    private boolean optional = true;
    private boolean repeatable;

}
