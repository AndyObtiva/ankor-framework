package at.irian.ankor.ref;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;

import java.util.List;

/**
 * @author Manfred Geiler
 */
public abstract class RefContextFactoryProvider {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefContextFactoryProvider.class);

    public abstract RefContextFactory createRefContextFactory(BeanResolver beanResolver,
                                                              List<ViewModelPostProcessor> viewModelPostProcessors,
                                                              Scheduler scheduler,
                                                              ModelRootFactory modelRootFactory);

}