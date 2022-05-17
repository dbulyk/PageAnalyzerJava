install: #Очищаем предыдущую сборку
	./gradlew clean install
	./gradlew install

run-dist: #Выполняем запуск файла
	./gradlew run

check-updates: #Проверяем обновления зависимостей
	chmod +x ./gradlew
	./gradlew dependencyUpdates

lint: #Проверяем стиль кода
	./gradlew checkstyleMain

clean: #Выполняем очищение проекта от предыдущих сборок
	./gradlew clean

.PHONY: build
build: #Выполняем сборку проекта
	./gradlew clean build
	make lint

report:
	./gradlew jacocoTestReport