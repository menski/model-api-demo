/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author Sebastian Menski
 */
public class ModifyInvoiceExample {

  public static void main(String[] args) {
    InputStream resourceAsStream = ModifyInvoiceExample.class.getResourceAsStream("invoice.bpmn");
    BpmnModelInstance modelInstance = Bpmn.readModelFromStream(resourceAsStream);

    Collection<Task> tasks = modelInstance.getModelElementsByType(Task.class);

    for (Task task : tasks) {
      task.setName("Task:\n" + task.getName());
      if (task instanceof UserTask) {
        System.out.printf("Assignee of task < %s > is < %s >\n",
          task.getId(), ((UserTask) task).getCamundaAssignee());
      }
    }

    ServiceTask serviceTask = modelInstance.newInstance(ServiceTask.class);
    serviceTask.builder()
      .id("bankTransfer")
      .name("Prepare for bank transfer")
      .camundaClass("org.camunda.bpm.BankTransferService");

    ModelElementInstance prepareBankTransfer = modelInstance.getModelElementById("prepareBankTransfer");
    prepareBankTransfer.replaceWithElement(serviceTask);

    Bpmn.writeModelToFile(new File("src/main/resources/modify.bpmn"), modelInstance);
  }


}
