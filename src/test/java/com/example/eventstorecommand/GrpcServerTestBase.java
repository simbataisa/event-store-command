package com.example.eventstorecommand;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.lognet.springboot.grpc.GRpcServerRunner;
import org.lognet.springboot.grpc.autoconfigure.GRpcServerProperties;
import org.lognet.springboot.grpc.context.LocalRunningGrpcPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Optional;

public abstract class GrpcServerTestBase {

  @Autowired(required = false)
  @Qualifier("grpcServerRunner")
  protected GRpcServerRunner grpcServerRunner;

  @Autowired(required = false)
  @Qualifier("grpcInprocessServerRunner")
  protected GRpcServerRunner grpcInprocessServerRunner;

  protected ManagedChannel channel;
  protected ManagedChannel inProcChannel;

  @LocalRunningGrpcPort
  protected int runningPort;

  @Autowired
  protected ApplicationContext context;

  @Autowired
  protected GRpcServerProperties gRpcServerProperties;

  @BeforeEach
  public void init() {
    if (gRpcServerProperties.isEnabled()) {
      ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("localhost", getPort());
      channelBuilder.usePlaintext();
      channel = onChannelBuild(channelBuilder).build();
    }
    if (StringUtils.hasText(gRpcServerProperties.getInProcessServerName())) {
      inProcChannel = onChannelBuild(InProcessChannelBuilder.forName(gRpcServerProperties.getInProcessServerName()).usePlaintext()).build();
    }
  }

  protected int getPort() {
    return runningPort;
  }

  protected ManagedChannelBuilder<?> onChannelBuild(ManagedChannelBuilder<?> channelBuilder) {
    return channelBuilder;
  }

  protected InProcessChannelBuilder onChannelBuild(InProcessChannelBuilder channelBuilder) {
    return channelBuilder;
  }

  @After
  public final void shutdownChannels() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdownNow);
    Optional.ofNullable(inProcChannel).ifPresent(ManagedChannel::shutdownNow);
  }
}
