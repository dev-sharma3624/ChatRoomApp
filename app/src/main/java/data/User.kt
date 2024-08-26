package data

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = ""
)

sealed class Result<out T> {

/*    out keyword gurantees that the type T value is read only after being instantiated
    this supports our logic because this class will only be used check whether an
    operation has been success or failure and we will not require any logical operations
    to be applied on it. Inheriting Result in subclass makes the the parent class a super
    or common type for the subclasses which we can use in functions where we want to check
    which of the possibilities represented by various subclasses has arisen. This is like
    creating your own custom checker to check what problem arrived in performing a job.*/

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}