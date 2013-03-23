# SwiftRiver Rules Processor
The Rules Processor is an application that:
* Screens a drop before it enters a river using a set of user-defined conditions 
* Performs some operation(s) on the drop if the (user-defined) conditions check out

SwiftRiver rules are based on the [Event Condition Action(ECA)](http://en.wikipedia.org/wiki/Event_condition_action) pattern.

# Building
Use maven to build

	$ maven clean package

## Rule Conditions and Actions
* Conditions

	The conditions are based on the following drop properties:

	* The title of the drop. In the case of an news article, this would be the heading
	* The content of the drop
	* Source of the drop e.g. name of the origin website, Twitter user etc

* Actions

	The supported rule actions are:
	
	* Mark the drop as read
	* Discard the drop from the (destination) river
	* Add the drop to a bucket

# License
Copyright 2013 Ushahidi Inc.

Licensed under AGPL v3. For a full copy of the license, see: http://www.gnu.org/licenses/agpl.html
 