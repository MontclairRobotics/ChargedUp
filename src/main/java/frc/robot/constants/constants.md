# How to Organize our Constants?

A few choices:

|*Level of Action*|*Action*|*Votes*|
|:-:|:-:|:-:|
|**Do Nothing**|Leave it as is and embrace the hellish landscape of subclasses|$0$|
|**Minimal**|Move all from `Constants.Robot` to `Constants`|$4$|
|**Medium**|Move each constants class to its own class;  `Constants.Robot.Stinger` $\rightarrow$ `StingerConstants`|$8$|
|**High (Abe)**|Move each constants class to a subclass of its corresponding class; `Constants.Robot.Stinger` $\rightarrow$ `Stinger.Constants`|$3$|
|**Maximal**|Move each constant to its corresponding class; `Constants.Robot.Stinger.MAX_HEIGHT` $\rightarrow$ `Stinger.MAX_HEIGHT`|$0$|
|**James is sus**|sus|$3$|