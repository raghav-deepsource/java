package com.structurizr.model;

final class CanonicalNameGenerator {

    private static final String PERSON_TYPE = "Person://";
    private static final String SOFTWARE_SYSTEM_TYPE = "SoftwareSystem://";
    private static final String CONTAINER_TYPE = "Container://";
    private static final String COMPONENT_TYPE = "Component://";

    private static final String DEPLOYMENT_NODE_TYPE = "DeploymentNode://";
    private static final String INFRASTRUCTURE_NODE_TYPE = "InfrastructureNode://";
    private static final String CONTAINER_INSTANCE_TYPE = "ContainerInstance://";
    private static final String SOFTWARE_SYSTEM_INSTANCE_TYPE = "SoftwareSystemInstance://";

    private static final String STATIC_CANONICAL_NAME_SEPARATOR = ".";
    private static final String DEPLOYMENT_CANONICAL_NAME_SEPARATOR = "/";

    private String formatName(Element element) {
        return formatName(element.getName());
    }

    private String formatName(String name) {
        return name
                .replace(STATIC_CANONICAL_NAME_SEPARATOR, "")
                .replace(DEPLOYMENT_CANONICAL_NAME_SEPARATOR, "");
    }

    String generate(Person person) {
        return PERSON_TYPE + formatName(person);
    }

    String generate(SoftwareSystem softwareSystem) {
        return SOFTWARE_SYSTEM_TYPE + formatName(softwareSystem);
    }

    String generate(Container container) {
        return CONTAINER_TYPE + formatName(container.getSoftwareSystem()) + STATIC_CANONICAL_NAME_SEPARATOR + formatName(container);
    }

    String generate(Component component) {
        return COMPONENT_TYPE + formatName(component.getContainer().getSoftwareSystem()) + STATIC_CANONICAL_NAME_SEPARATOR + formatName(component.getContainer()) + STATIC_CANONICAL_NAME_SEPARATOR + formatName(component);
    }

    String generate(DeploymentNode deploymentNode) {
        StringBuilder buf = new StringBuilder();
        buf.append(DEPLOYMENT_NODE_TYPE);

        buf.append(formatName(deploymentNode.getEnvironment()));
        buf.append(DEPLOYMENT_CANONICAL_NAME_SEPARATOR);

        DeploymentNode parent = (DeploymentNode)deploymentNode.getParent();
        while (parent != null) {
            buf.append(formatName(parent));
            buf.append(DEPLOYMENT_CANONICAL_NAME_SEPARATOR);
            parent = (DeploymentNode)parent.getParent();
        }

        buf.append(formatName(deploymentNode));

        return buf.toString();
    }

    String generate(InfrastructureNode infrastructureNode) {
        String deploymentNodeCanonicalName = generate((DeploymentNode)infrastructureNode.getParent()).substring(DEPLOYMENT_NODE_TYPE.length());

        return INFRASTRUCTURE_NODE_TYPE + deploymentNodeCanonicalName + DEPLOYMENT_CANONICAL_NAME_SEPARATOR + formatName(infrastructureNode);
    }

    String generate(SoftwareSystemInstance softwareSystemInstance) {
        String deploymentNodeCanonicalName = generate((DeploymentNode)softwareSystemInstance.getParent()).substring(DEPLOYMENT_NODE_TYPE.length());

        return SOFTWARE_SYSTEM_INSTANCE_TYPE + deploymentNodeCanonicalName + DEPLOYMENT_CANONICAL_NAME_SEPARATOR + formatName(softwareSystemInstance.getSoftwareSystem()) + "[" + softwareSystemInstance.getInstanceId() + "]";
    }

    String generate(ContainerInstance containerInstance) {
        String deploymentNodeCanonicalName = generate((DeploymentNode)containerInstance.getParent()).substring(DEPLOYMENT_NODE_TYPE.length());

        return CONTAINER_INSTANCE_TYPE + deploymentNodeCanonicalName + DEPLOYMENT_CANONICAL_NAME_SEPARATOR + generate(containerInstance.getContainer()).substring(CONTAINER_TYPE.length()) + "[" + containerInstance.getInstanceId() + "]";
    }

}