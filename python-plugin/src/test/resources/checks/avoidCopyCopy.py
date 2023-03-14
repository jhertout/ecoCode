import copy
import array

class TestObject():

    def __init__(self, prop1, prop2):
        self.prop1 = prop1
        self.prop2 = prop2

    def __str__(self):
        return self.prop1 + " " +  self.prop2

    def listFromObject(self):
        return [self.prop1, self.prop2]

    def copy(self, list):
        return list


# List copy

list1 = [1, 2, [3, 4], 5]
list_copy1 = copy.copy(list1) # Noncompliant {{Avoid using Lib/copy.copy(x)}}

list2 = [TestObject(1, "test1"), TestObject(2, "test2")]
list_copy2 = copy.copy(list2) # Noncompliant {{Avoid using Lib/copy.copy(x)}}

list3 = TestObject(1, "test1").listFromObject()
list_copy3 = copy.copy(list3) # Noncompliant {{Avoid using Lib/copy.copy(x)}}

# Array copy

array1 = array.array('i', [1, 2, 3])
array_copy1 = copy.copy(array1) # NO issue

# Object copy

object1 = "test"
object_copy1 = copy.copy(object1) # NO issue

object2 = TestObject("test 1", "test 2")
object_copy2 = copy.copy(object2) # NO issue

# Other copy()

object3 = TestObject("test 1", "test 2")
#other_list = object3.copy([1, 2]) # NO issue
