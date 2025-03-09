
package org.example;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.iam.model.CreateUserRequest;
import software.amazon.awssdk.services.iam.model.CreateUserResponse;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.model.DeleteUserRequest;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.waiters.IamWaiter;
import software.amazon.awssdk.services.iam.model.GetUserRequest;
import software.amazon.awssdk.services.iam.model.GetUserResponse;

//classe principale
public class Main {
    public static void main(String[] args) {

        // creation d'un utilisateur
        public class CreateUser {
            final String usage = """

                Usage:
                    <username>\s

                Where:
                    username - The name of the user to create.\s
                """;

                if (args.length != 1) {
                System.out.println(usage);
                System.exit(1);
            }

            String username = args[0];
            Region region = Region.AWS_GLOBAL;
            IamClient iam = IamClient.builder()
                    .region(region)
                    .build();

            String result = createIAMUser(iam, username);
                System.out.println("Successfully created user: " + result);
                iam.close();
        }

        public static String createIAMUser(IamClient iam, String username) {
            try {
                // Create an IamWaiter object.
                IamWaiter iamWaiter = iam.waiter();

                CreateUserRequest request = CreateUserRequest.builder()
                        .userName(username)
                        .build();

                CreateUserResponse response = iam.createUser(request);

                // Wait until the user is created.
                GetUserRequest userRequest = GetUserRequest.builder()
                        .userName(response.user().userName())
                        .build();

                WaiterResponse<GetUserResponse> waitUntilUserExists = iamWaiter.waitUntilUserExists(userRequest);
                waitUntilUserExists.matched().response().ifPresent(System.out::println);
                return response.user().userName();

            } catch (IamException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
            return "";
        }


        // suppression d'un utilisateur

        public class DeleteUser {
            final String usage = """

                Usage:
                    <userName>\s

                Where:
                    userName - The name of the user to delete.\s
                """;

                if (args.length != 1) {
                System.out.println(usage);
                System.exit(1);
            }

            String userName = args[0];
            Region region = Region.AWS_GLOBAL;
            IamClient iam = IamClient.builder()
                    .region(region)
                    .build();

            deleteIAMUser(iam, userName);
                System.out.println("Done");
                iam.close();
        }

        public static void deleteIAMUser(IamClient iam, String userName) {
            try {
                DeleteUserRequest request = DeleteUserRequest.builder()
                        .userName(userName)
                        .build();

                iam.deleteUser(request);
                System.out.println("Successfully deleted IAM user " + userName);

            } catch (IamException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
        }

    }

}
