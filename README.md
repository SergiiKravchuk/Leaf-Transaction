# Welcome, Spring lover 🧑‍💻

This repository contains practice exercise to get familiar with Dynamic Proxies and how Spring uses them to customize Beans.

## Prerequisites:
* [JDK 11+](https://jdk.java.net/11/)
* [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

## How to start practicing? 💪
* check the [onboarding presentation](https://github.com/SergiiKravchuk/Leaf-Transaction/blob/practicing/Codeus%20Leaf%20Transaction%20Short.pdf) on Spring Framework internals and Dynamic Proxy.
* choose `practicing` branch - contains everything you need for practice: READMEs, general docs, and tests.
* read [Task Description](README.md#task-description) section below to know main and extra goals, and rules.
* run `org.codeus.leaf_transaction.ApplicationContextTest` to verify your implementation.

If you are stuck, you can:
* check the [Hints](README.md#hints) section below.
* check the `completed` branch to see the completed implementation.

### Goodies
If you want to present this topic to an audience, use [full onboarding presentation](https://github.com/SergiiKravchuk/Leaf-Transaction/blob/practicing/Codeus%20Leaf%20Transaction.pptx).

___
## Task Description

### Goal
Replace manual transaction handling in `org.codeus.leaf_transaction.ParticipantStorageService` with a Proxy.

Steps:
1. Create @Transactional annotation. 
2. Proxy that wraps target object and handles transaction using `org.codeus.leaf_transaction.datasource.SingleConnectionDataSource`
3. Implement logic in `org.codeus.leaf_transaction.ApplicationContext` that checks for the presence of the `@Transactional` annotation and wrap the corresponding classes in a Proxy at runtime.

### Rules
1. The main changes are expected to be made to `org.codeus.leaf_transaction.ParticipantStorageService`, `org.codeus.leaf_transaction.ApplicationContext`, and your own/new classes. All other classes should remain unchanged.
2. Your solution should create a Proxy only for classes that have methods annotated with `@Transactional`. 
3. Your solution should be able to work with simple Beans (e.g. `org.codeus.leaf_transaction.NonTransactionalSampleService`)

### Extra goals:
1. use [`Reflections` util](https://mvnrepository.com/artifact/org.reflections/reflections/0.10.2) to replace the hardcoded `if-clause` with package scan.
2. use java.lang.reflect.Proxy#newProxyInstance to create a dynamic-proxy


## Hints:
<details> 
  <summary>Hint 1: Recommendation on storing beans </summary>
   1. Create a class that will serve as a container for the Bean instance and its interfaces from the original class (not Proxy). 
   2. Add method - hasInterface - that accepts interface as Class<?>, and checks if that given interface is in list of the preserved interfaces. If yes, return true - client asks for this Bean.
   2. Wrap all available Beans in this container and store them all as a list.
   3. In ApplicationContext.getBean, go through the list and call that hasInterface to verify if the ApplicationContext contains requested Bean.
</details>
<details> 
  <summary>Hint 2: How to check if method is annotated with @Transactional in InvocationHandler? </summary>
   - When creating an instance of the InvocationHandler, pass the original bean class and preserve it in the InvocationHandler. Then use this class to retrieve methods annotated with @Transactional.
</details>

Interested in more tasks like this 🤔? Join [Codeus Community at Discord](https://discord.com/invite/WpUc2ZYHmE)