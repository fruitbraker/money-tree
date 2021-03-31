# Money Tree - Budgeting and Expense Tracker
![Money Tree](https://github.com/fruitbraker/money-tree/workflows/money-tree/badge.svg?event=push)

See the frontend application [here](https://github.com/fruitbraker/money-tree-ui).

# Running this application locally

Please note that this project is still under **active** development, so no releases yet. 
You are welcome to download the source code and play around with the current state.

For now, the following steps are for those running on Windows. Ideally, you would have 
the [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10) V2 installed.
For linux users, I trust that you can get it up and running in that environment. I don't have a Linux environment up and running 
yet to write instructions. 


## Steps

1. Install [Docker Desktop for Windows](https://docs.docker.com/docker-for-windows/install/)
2. Install IntelliJ. We're going to have to run the backend server through this IDE for now.
3. *Optional* - If you already have a RESTful client installed, you may skip this step. 
   If not, I recommend using [Insomnia](https://insomnia.rest/). [Postman]() is also a good option.
4. Clone this repository
5. Have IntelliJ open the `build.gradle` file and open is as project. Let it sit to install any dependencies.
6. With Docker Desktop, open the `docker-compose.yml` and hit play. This will spin up a Postgres server and pgAdmin.
7. To run the project, click play when you open the `MtApiMain.kt` file in IntelliJ.
8. Run free with the RESTful lient you have installed. Alternatively, you can use your web browser but that's limited.

## Using the `mt-port.ipynb`

This notebook provides an easy way to port a `.tsv` file of existing expenses (like from Excel) into the database. 
The notebook doesn't actually run any Python but instead uses bash commands. The port will populate Vendors, Expense Category, and Expenses.

There are a couple requirements for this to work:

1. Have Jupyter notebook installed.
2. Have a linux kernel installed for Jupyter
3. The `.tsv` needs to be in the format (with no column headers):
```
Transaction_Date    Vendor  Transaction_Amount  Expense_Category    Notes(optional)
```

# Deep dive into Money Tree

## Schema

You can reference the `swagger.yml` [here](https://github.com/fruitbraker/money-tree/blob/master/persist/src/swagger.yml).
However, it might not be up to date.

## Architecture

The backend service is structured as "domain driven design" and follows the hexagonal architecture (also known as ports and adapters). 
This gives the flexibility of swapping out anything without refactoring the entire project. A classic example is changing the database.

### DDD, aka Domain Driven Design

The "domain" or "business logic", is the brains and central part of the backend service. Normally, this is where business requirements are fulfilled. It is also important to note that
there changes here are minimal because that would normally require major code refactoring across a lot of files.

In the context of Money Tree, some example requirements are:
1. Retrieve entities from the database
2. Add new entities to the database
3. Update new entities from the database
4. Delete entities from the database 

Normally, these requirements are defined in interfaces.

### Hexagonal Architecture and Inversion of Control

The name sounds like the name - there is a hexagon in the center, with the right side and left side populated with stuff.

![image](https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fjmgarridopaz.github.io%2Fassets%2Fimages%2Fhexagonalarchitecture%2Ffigure1.png&f=1&nofb=1)

The domain will have public interfaces that dependencies would use to interact with the business logic. For brevity, let's say there is an
interface with a function for `get()`. A module that communicates with the database will implement that interface - basicallly having the domain logic, 
or business logic, define how the interaction with the database should be. 

This is known as **Inversion of Control**.

## Technologies used

### PostgresSQL
A classic relational database already in widespread use. Since the data for Money Tree is already relational (Expenses <-> Vendor, Expenses <-> Expense Category, etc), this 
was a no brainer. I *could have* used something else, say like Mongo, but that is something entirely different. 

A subtle, yet very important, requirement everyone needs to consider when choosing a database technology is the [CAP theorem](https://en.wikipedia.org/wiki/CAP_theorem). 
I believe that Money Tree, or anything that deals with finances, would need to mee

### http4k
There are like a billion libraries to expose http endpoints. To name a few:
1. SpringBoot - yes, this is also more than just exposing http endpoints
2. Micronaut - same as Spring...
3. ktor
4. ??? - your favorite one?

I chose to go for http4k because it is incredibly easy to use and very lightweight. The functional aspect is very useful if 
your service requires a lot of pre/post http request processing (through the use of filters).

### Wait...no frameworks?
Money Tree does not use any frameworks (SpringBoot, Micronaut, etc) or any third-party libraries for dependency injections like Guice.
There's nothing wrong with using SpringBoot, however it blackboxes a lot of work behind the scenes through annotations to the point 
it feels like black magic. It's great to quickly spin up applications, but, at the expense of some flexibility.

There are disadvantages to not using frameworks. Initialization could get reeaallyy long (`see MtApiMain.kt`) when gluing dependencies together.
However, I think this is a good tradeoff so that you know what is going on and have greater control of dependencies.

Money-Tree has an [older implementation](https://github.com/fruitbraker/money-tree-old) that uses `Guice` for dependency injection. While it *worked*, the project was 
dependent on a third-party library, meaning any bugs present there would also be present in Money Tree. I did not like that because there was a bug that wasn't fixed until 
the v5 release of `Guice`, which took forever. This isn't meant to discourage others from using Guice or any libraries, it's still a great library. 

There are many paths to a waterfall...this particular implementation of Money Tree is just one of them.
