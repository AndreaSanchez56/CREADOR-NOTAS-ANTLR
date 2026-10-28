# NotaLab — aplicación web de notas con ANTLR 4

Aplicación web para introducir tantas notas como quieras sin editar el código fuente. Editor de partitura visual, botones de agregar/eliminar, silencios, duraciones, reproducción con Web Audio, exportación MIDI y `.mus`.

## Arquitectura
- **ANTLR 4 real**: `src/main/antlr4/Musica.g4`, compilada con el plugin de Maven; `MusicaLexer` y `MusicaParser` validan el texto en `POST /api/validar`.
- **Servidor Java 17**: `src/main/java/com/musica/WebApp.java`.
- **Interfaz web**: `src/main/resources/public/index.html`.
- La interfaz también funciona abriendo el HTML directamente, pero **la validación ANTLR solo funciona con el servidor Java activo**. El dibujo de partitura no es un motor de notación profesional: presenta una línea melódica en clave de sol.

## Ejecutar en Linux

```bash
sudo apt install openjdk-17-jdk maven
cd creador-notas-web-antlr
mvn clean package
java -jar target/creador-notas-web-antlr-1.0.0.jar
```

Abre **http://localhost:8080** desde el navegador. Si ya hay otro servicio en ese puerto: `PORT=8090 java -jar target/creador-notas-web-antlr-1.0.0.jar`.

## Publicar para entrar desde internet

Necesitas desplegar el proyecto en un proveedor de hosting (no se publica automáticamente al descargar el ZIP). Por ejemplo, súbelo a un repositorio GitHub y crea un **Web Service** en Render con **Runtime Docker** y el `Dockerfile` de la raíz. Al finalizar el despliegue, Render proporciona una URL pública que puedes abrir en cualquier navegador. Las condiciones y planes del proveedor pueden variar.

## Ejemplos de texto

```
TEMPO 120;
C4;
D4;
E4 BLANCA;
F#4 CORCHEA;
SILENCIO NEGRA;
A3;
```

La nota y su duración se seleccionan en la interfaz. No hace falta editar `Musica.g4` para agregar melodías.

## Nota técnica

La reproducción requiere un gesto del usuario (botón Reproducir), como exigen los navegadores. Los datos de la melodía se mantienen en la pestaña actual; exporta el `.mus` o `.mid` para conservarlos.

## Publicar en Render

1. Sube el contenido de esta carpeta a un repositorio GitHub, con `Dockerfile`, `pom.xml`, `render.yaml` y `src/` en la raíz.
2. En Render: New > Web Service > conectar el repositorio.
3. Elige **Docker**, plan **Free** (si está disponible) y crea el servicio. En configuración avanzada, establece `PORT=10000` si no utilizas Blueprint.
4. Render compila con Maven y genera `MusicaLexer` y `MusicaParser` desde `src/main/antlr4/Musica.g4`.
5. Cuando el deploy termine, abre la URL `https://<nombre-asignado>.onrender.com`.

Si deseas usar Blueprint, escoge New > Blueprint y conecta el repositorio; Render leerá `render.yaml`.

NOTA: El navegador puede crear y reproducir notas, y la API `/api/validar` realiza la validación real de ANTLR en el servidor. El servicio Free puede dormirse tras periodos sin visitas.
