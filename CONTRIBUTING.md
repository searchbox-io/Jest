#Contributing to Jest

We'd love for you to contribute to our source code and to make it even better than it is
today! Here are the guidelines we'd like you to follow:

 - [Code of Conduct](#coc)
 - [Question or Problem?](#question)
 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Submission Guidelines](#submit)
 - [Coding Rules](#rules)

## <a name="coc"></a> Code of Conduct
Help us keep Jest open and inclusive. Please read and follow our [Code of Conduct][coc].

## <a name="question"></a> Got a Question or Problem?
If you have questions about how to use Jest, please direct these to [StackOverflow][stackoverflow] or the [GitHub issue tracker][issuetracker].

## <a name="issue"></a> Found an Issue?
If you find a bug in the source code or a mistake in the documentation, you can help us by submitting an issue to our [GitHub issue tracker][issuetracker]. 
Even better you can submit a Pull Request with a fix. 

## <a name="feature"></a> Want a Feature?
You can request a new feature by submitting an issue to our [GitHub issue tracker][issuetracker].

## <a name="submit"></a> Submission Guidelines

### Submitting an Issue
Before you submit your issue search the archive, maybe your question was already answered.

If your issue appears to be a bug, and hasn't been reported, open a new issue.
Help us to maximize the effort we can spend fixing issues and adding new
features, by not reporting duplicate issues. Providing the following information will increase the
chances of your issue being dealt with quickly:

* **Overview of the Issue** - if an error is being thrown a stack trace helps
* **Motivation for or Use Case** - explain why this is a bug for you
* **Version(s)** - is it a regression?
* **Reproduce the Error** - provide a live example using a (JUnit) test case or an unambiguous set of steps.
* **Related Issues** - has a similar issue been reported before?
* **Suggest a Fix** - if you can't fix the bug yourself, perhaps you can point to what might be
  causing the problem (line of code or commit)

### Submitting a Pull Request
Before you submit your pull request consider the following guidelines:

* Search [GitHub issue tracker][issuetracker] for an open or closed Pull Request
  that relates to your submission. You don't want to duplicate effort.
* Make your changes in a new git branch

     ```shell
     git checkout -b my-fix-branch master
     ```

* Create your patch, **including appropriate test cases**.
* Follow our [Coding Rules](#rules).
* Commit your changes using a descriptive commit message that includes the related issue IDs.

     ```shell
     git commit -a
     ```
  Note: the optional commit `-a` command line option will automatically "add" and "rm" edited files.

* Run the full test suite and ensure that all tests pass.

* Push your branch to GitHub:

    ```shell
    git push origin my-fix-branch
    ```

* In GitHub, send a pull request to `angular:master`.
* If we suggest changes on your pull request then
  * Make the required updates.
  * Re-run the full test suite to ensure tests are still passing.
  * Rebase your branch and force push to your GitHub repository (this will update your Pull Request):

    ```shell
    git rebase master -i
    git push -f
    ```

That's it! Thank you for your contribution!

#### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull the changes
from the main (upstream) repository:

* Delete the remote branch on GitHub either through the GitHub web UI or your local shell as follows:

    ```shell
    git push origin --delete my-fix-branch
    ```

* Check out the master branch:

    ```shell
    git checkout master -f
    ```

* Delete the local branch:

    ```shell
    git branch -D my-fix-branch
    ```

* Update your master with the latest upstream version:

    ```shell
    git pull --ff upstream master
    ```
    
## <a name="rules"></a> Coding Rules
To ensure consistency throughout the source code, keep these rules in mind as you are working:

* All features or bug fixes **must be unit and integration tested**.
* All public API methods that are not inherently obvious (e.g.: parameters expected in a specific format, usage has a complex pattern, default behaviour may be unexpected, etc.)  **should have Javadoc**. 
* Code style (braces placement, naming convention, error handling, logging, tab-space preference) should **follow the general style** used in project.
* Refrain from making **breaking changes** unless it is discussed and agreed upon.
* Be extremely cautious when using (or do not use at all) your IDE's auto-formatter. Your **commit must not include (auto) cosmetic changes** (e.g.: changes on code-block order or statement order, etc.) unless they contribute functionally. 



[coc]: https://github.com/searchbox-io/Jest/blob/master/CODE_OF_CONDUCT.md
[stackoverflow]: http://stackoverflow.com/questions/tagged/jest
[issuetracker]: http://stackoverflow.com/questions/tagged/jest