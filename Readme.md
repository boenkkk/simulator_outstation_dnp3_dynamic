# DNP3 Outstation Simulator

DNP3 Outstation Simulator

## Getting Started

### Dependencies

- Java 21
- Apache Maven

### Executing program

- Run

```
mvn clean spring-boot:run
```

- Build

```
mvn clean package
```

- Deploy Using Docker Compose (Single Container)

```
docker compose up --build -d
```

- Replicate Docker Container (Multiple Container)

```
chmod +x scale-container.sh
```

```
./scale-container.sh
```

```
sh scale-container.sh
```

## Help

If has errors

```
{maven_command} -X
```

Re-run to full debug logging

## Conventional Commit

Do Before Commit:

- Check code convention & file naming
- Remove unused code, imports, or variable
- Check warning
- Add TODO if code isnt finished
- Keep code to clean

### New Feature or Update Feature

```
[new|patch]{file name or feautre name}
-description
```

example:

```
[new]httputil
-method postReq exception cant throws

[patch]httputil
-method postReq exception now can throws
-add method getReq

[new]measure analog input
-method handleAnalogInput cant call httputil

[patch]measure analog input
-method handleAnalogInput now can call httputil
-add method handleBinaryInput
```

## Authors

- [Budi Santoso](https://blog.boenkkk.dev/)

## Acknowledgments

- [README-Template.md](https://gist.github.com/DomPizzie/7a5ff55ffa9081f2de27c315f5018afc)
