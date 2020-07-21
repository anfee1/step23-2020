
SHELL=/bin/bash
DART_CLI?=dart
FLUTTER_CLI?=flutter
MAVEN_CLI?=mvn
FLUTTER_APP_ROOT?=frontend/ui
MAVEN_FRONTEND_ROOT?=frontend
CLANG_FORMAT?=clang-format

# Is there a better way to do this??
# From https://stackoverflow.com/questions/4036191
rwildcard=$(wildcard $1$2) $(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2))

format:
	$(CLANG_FORMAT) --style=Google -i --sort-includes $(call rwildcard,frontend/,*.java)
	$(DART_CLI) format $(FLUTTER_APP_ROOT)

clean:
	pushd $(FLUTTER_APP_ROOT) && $(FLUTTER_CLI) clean && popd
	$(MAVEN_CLI) clean --file $(MAVEN_FRONTEND_ROOT)/pom.xml

flutter-pub-get:
	pushd $(FLUTTER_APP_ROOT) && $(FLUTTER_CLI) pub get && popd

build-and-copy-flutter: flutter-pub-get
	pushd $(FLUTTER_APP_ROOT) && $(FLUTTER_CLI) build web && popd
	cp -r $(FLUTTER_APP_ROOT)/build/web/* $(MAVEN_FRONTEND_ROOT)/src/main/webapp/

test-flutter: flutter-pub-get
	pushd $(FLUTTER_APP_ROOT) && $(FLUTTER_CLI) test && popd

test-frontend-server:
	$(MAVEN_CLI) test --file $(MAVEN_FRONTEND_ROOT)/pom.xml

test: test-flutter test-frontend-server

package: build-and-copy-flutter
	$(MAVEN_CLI) package --file $(MAVEN_FRONTEND_ROOT)/pom.xml

run-local-frontend: build-and-copy-flutter
	$(MAVEN_CLI) package appengine:run --file $(MAVEN_FRONTEND_ROOT)/pom.xml
