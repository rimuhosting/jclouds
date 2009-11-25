package org.jclouds.rimuhosting.miro;

import com.google.common.base.Predicate;
import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.data.CreateOptions;
import org.jclouds.rimuhosting.miro.data.NewInstance;
import org.jclouds.rimuhosting.miro.domain.Instance;
import org.jclouds.rimuhosting.miro.domain.NewInstanceResponse;
import org.jclouds.ssh.SshClient.Factory;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.net.InetSocketAddress;


public class RimuHostingComputeClient {
   @Resource
   protected Logger logger = Logger.NULL;

   private final Predicate<InetSocketAddress> socketTester;
   private final RimuHostingClient rhClient;

   @Inject
   public RimuHostingComputeClient(RimuHostingClient rhClient, Factory sshFactory,
            Predicate<InetSocketAddress> socketTester) {
      this.rhClient = rhClient;
      this.sshFactory = sshFactory;
      this.socketTester = socketTester;
   }

   private final Factory sshFactory;


   public Long start(String name, String planId, String imageId) {
      logger.debug(">> instantiating RimuHosting VPS name(%s) plan(%s) image(%s)", name, planId, imageId);
      NewInstanceResponse instanceRespone = rhClient.createInstance(new NewInstance(new CreateOptions(name,null,imageId), planId));
      logger.debug(">> VPS id(%l) started and running.");
      return instanceRespone.getInstance().getId();
   }



   public void reboot(Long id) {
      Instance instance = rhClient.getInstance(id);
      logger.debug(">> rebooting VPS(%l)", instance.getId());
      rhClient.restartInstance(id);
      logger.debug("<< on VPS(%l)", instance.getId());
   }

   public void destroy(Long id) {
      Instance instance = rhClient.getInstance(id);
      logger.debug(">> destroy VPS(%l)", instance.getId());
      rhClient.destroyInstance(id);
      logger.debug(">> destroyed VPS");
   }
}