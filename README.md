# Custom RPC Framework with Spring Boot and Netty

## Overview

This project implements a custom distributed Remote Procedure Call (RPC) framework built using Java, Spring Boot, and Netty. It facilitates seamless and high-performance communication between microservices by leveraging non-blocking I/O and efficient serialization. Key features include dynamic service discovery via Zookeeper, client-side load balancing, and clean integration with the Spring ecosystem using AOP principles.

## Technology Stack

*   **Core Framework:** Spring Boot 2.7.18
*   **Networking:** Netty 4.1.x (NIO, Event-Driven)
*   **Service Discovery & Registry:** Apache Zookeeper (using `zkclient`)
*   **Serialization:** Protostuff
*   **Proxy Generation:** CGLIB
*   **Build Tool:** Apache Maven
*   **Language:** Java 8
*   **Utilities:** Lombok, Guava, Apache Commons Lang3

## Architecture & Workflow

The framework consists of three main modules:

1.  **`rpc-shared`:** Contains common code used by both client and server, including:
    *   Service interface definitions (e.g., `OrderService`).
    *   Data transfer objects (`RpcRequest`, `RpcResponse`).
    *   Netty codecs for serialization/deserialization using Protostuff (`RpcRequestSerializer`, `RpcResponseDeserializer`, etc.).
    *   Custom annotations (`@MyRpcService`).
2.  **`rpc-server`:** Hosts the actual service implementations.
    *   Responsible for starting a Netty server to listen for incoming RPC requests.
    *   Registers its available services with Zookeeper upon startup.
3.  **`rpc-client`:** Consumes remote services.
    *   Discovers available service providers from Zookeeper.
    *   Uses dynamic proxies (CGLIB) to make remote calls look like local method invocations.
    *   Manages Netty connections to service providers and handles load balancing.

### Workflow Breakdown:

1.  **Server Startup & Service Registration:**
    *   The `rpc-server` application starts (Spring Boot).
    *   `ZkRegistry` scans for beans annotated with `@MyRpcService`.
    *   For each annotated service, it registers the server's IP address and port under a specific path in Zookeeper (e.g., `/com.example.rpc.services.OrderService/192.168.1.10:8080`).
    *   `RpcNettyServer` starts, initializing Netty's event loops (boss, worker, business) and configuring the server pipeline with framing handlers, Protostuff codecs, and the `RequestHandler`.

2.  **Client Startup & Service Discovery:**
    *   The `rpc-client` application starts (Spring Boot).
    *   `ZkServiceDiscovery` queries Zookeeper for registered services and their provider nodes.
    *   The discovered provider information (IP, port) is stored in `ServiceProviderCache`.
    *   The client subscribes to Zookeeper updates to keep the cache synchronized with changes in service availability.

3.  **Client RPC Call:**
    *   A client component (e.g., a Controller) has a field annotated with `@RpcServiceProxy` (e.g., `@RpcServiceProxy private OrderService orderService;`).
    *   `RpcClientBeanProcessor` (a `BeanPostProcessor`) detects this annotation and injects a CGLIB proxy instance created by `RpcServiceProxyFactory`.
    *   When a method is called on the proxy (`orderService.createOrder(...)`), the CGLIB `MethodInterceptor` intercepts it.
    *   The interceptor builds an `RpcRequest` object containing the service name, method name, arguments, and a unique request ID.
    *   It calls `RpcRequestManager.sendRequest(request)`.

4.  **Request Transmission & Load Balancing:**
    *   `RpcRequestManager` retrieves the list of available `ServiceProvider` instances for the requested service from the cache.
    *   It uses a configured `LoadBalanceStrategy` (e.g., `RandomStrategy`) provided by `StrategyProvider` to select one provider.
    *   It establishes or reuses a Netty `Channel` to the selected provider (managed by `RpcRequestContainer`).
    *   The `RpcRequest` is serialized using `RpcRequestSerializer` (Protostuff) and sent over the Netty channel.
    *   A `RequestPromise` is stored locally, mapping the request ID to a future result.

5.  **Server Request Processing:**
    *   The `rpc-server`'s Netty pipeline receives the request bytes.
    *   `LengthFieldBasedFrameDecoder` reconstructs the complete message frame.
    *   `RpcRequestDeserializer` deserializes the bytes into an `RpcRequest` object using Protostuff.
    *   The `RequestHandler` receives the `RpcRequest`. It uses reflection (or a pre-built mapping) to find the target service bean (e.g., `OrderServiceImpl`) and invoke the requested method with the provided parameters.
    *   The result (or exception) from the service implementation is wrapped in an `RpcResponse` object.

6.  **Response Transmission & Handling:**
    *   The `RpcResponse` is serialized by `RpcResponseSerializer` (Protostuff) on the server.
    *   `LengthFieldPrepender` adds the length header, and the response bytes are sent back to the client via the Netty channel.
    *   The `rpc-client`'s Netty pipeline receives the response bytes.
    *   `LengthFieldBasedFrameDecoder` reconstructs the message.
    *   `RpcResponseDeserializer` deserializes the bytes into an `RpcResponse` object.
    *   `RpcResponseHandler` retrieves the corresponding `RequestPromise` using the response's request ID and completes it with the received `RpcResponse`.
    *   The `RpcRequestManager`'s blocked `sendRequest` call receives the `RpcResponse` from the fulfilled promise.
    *   The CGLIB interceptor extracts the result from the `RpcResponse` and returns it to the original caller.

## Key Features

*   **Custom RPC Framework:** Provides a complete mechanism for defining, exposing, and consuming services remotely.
*   **High Performance:** Leverages Netty's asynchronous, event-driven architecture, non-blocking I/O, and managed thread pools for low latency and efficient resource utilization.
*   **Dynamic Service Discovery:** Uses Zookeeper for automatic registration of service providers and discovery by clients. Clients adapt dynamically to changes in provider availability.
*   **Client-Side Load Balancing:** Implements configurable strategies (e.g., Random) for distributing requests across available service instances.
*   **Efficient Serialization:** Uses Protostuff for fast and compact binary serialization of request/response objects.
*   **Spring Boot Integration:** Seamlessly integrates with the Spring Boot ecosystem for dependency injection, configuration, and component lifecycle management.
*   **AOP for Cleanliness:** Utilizes Spring AOP (`BeanPostProcessor`) and CGLIB proxies to abstract away RPC complexity from the client code, making remote calls transparent.

## How to Use (Conceptual)

1.  **Define Service:** Create a service interface in the `rpc-shared` module (e.g., `OrderService.java`).
2.  **Implement Service:** Create an implementation class in the `rpc-server` module, annotate it with `@MyRpcService(interfaceClass = OrderService.class)`, and implement the interface methods (e.g., `OrderServiceImpl.java`).
3.  **Consume Service:** In the `rpc-client` module, declare a field of the interface type and annotate it with `@RpcServiceProxy` (e.g., `@RpcServiceProxy private OrderService orderService;`). Spring will inject a proxy.
4.  **Call Service:** Invoke methods on the injected proxy field as if it were a local object (e.g., `orderService.getOrderById(123)`). The framework handles the remote communication transparently.
