package net.oliver.sodi.spring;

import net.oliver.sodi.mail.MailBoxMonitor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class SodiApplicationListener implements ApplicationListener<ContextRefreshedEvent> {


    public static ApplicationContext applicationContext = null;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(applicationContext == null){
            applicationContext = contextRefreshedEvent.getApplicationContext();
        }

        MailBoxMonitor monitor = applicationContext.getBean(MailBoxMonitor.class);

       Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    monitor.start();
                }
            });
       thread.start();
    }
}
