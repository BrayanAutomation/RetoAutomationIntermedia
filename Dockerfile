# Imagen de ejecución para la suite de automatización de este repositorio
# (RetoAutomationIntermedia). NO construye el sistema bajo prueba — eso lo
# hace el docker-compose.yml del reto (automation-testing-artefacts).
#
# Se usa la imagen oficial de Playwright para Java porque su versión de
# navegadores debe coincidir exactamente con la librería
# com.microsoft.playwright:playwright resuelta por serenity-screenplay-playwright
# (ver ui-tests/build.gradle -> actualmente 1.58.0). Usar una imagen base
# genérica + "playwright install" manual es más frágil: basta un desfase de
# versión para que Playwright falle con "Executable doesn't exist".
#
# Trae JDK, navegadores (Chromium/Firefox/WebKit) y sus dependencias del SO
# ya instalados, por lo que no hace falta instalar nada adicional aquí.
FROM mcr.microsoft.com/playwright/java:v1.58.0-noble

WORKDIR /workspace

# El código fuente NO se copia a la imagen: se monta como volumen en tiempo
# de ejecución (ver docker-compose.tests.yml). Así no hay que reconstruir la
# imagen ante cada cambio de código, y el wrapper de Gradle (./gradlew)
# descarga/cachea la distribución de Gradle y el toolchain JDK 17 la primera
# vez que corre, reutilizando el volumen "gradle-cache" en corridas
# posteriores.
