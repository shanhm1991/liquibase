package liquibase.command

import liquibase.command.core.InternalHistoryCommandStep
import spock.lang.Specification

class CommandResultsBuilderTest extends Specification {

    def "builder works"() {
        setup:
        def outputStream = new ByteArrayOutputStream()
        def scope = new CommandScope("history")
        scope.setOutput(outputStream)
        def builder = new CommandResultsBuilder(scope)

        when:
        builder.getOutputStream().write("getOutputStream output".bytes)
        builder.addResult("a", "result from a")
        builder.addResult("bool", true)
        builder.addResult(InternalHistoryCommandStep.DEPLOYMENTS_RESULT, new InternalHistoryCommandStep.DeploymentHistory())

        builder.build()

        then:
        new String(outputStream.toByteArray()) == "getOutputStream output"
        builder.commandScope.command.name == ["history"] as String[]



    }
}
