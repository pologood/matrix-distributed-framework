


api processor 里面封装了所有的与api相关的处理类。原则上来讲，每个业务处理对应一个**************Processor.class

privates 包下，放置所有与公司内部使用的api，这部分api权限高，仅供内部客户端使用|根据业务情况再细分子包

publics 包下，放置开放给第三方的api接口，比如其他公司需要调用我方接口来同步数据的情况，功能类似于open-api的功能。|根据业务情况再细分子包


common包下，放置与API相关的公共接口









