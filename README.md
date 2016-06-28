# cg - Java Ray-Tracer / Path-Tracer
### TP Computacion Grafica

Para generar el archivo `.jar`, ejecutar:
```bash
$ mvn pacakge
```
en el directorio raiz del proyecto.

### Opciones de CLI:
*  `-i <path>`: Path de la escena a renderizar (formato [SSD](https://github.com/mmerchante/edutracing)).
*  `-o <path>`: Path de la imagen a crear.  De ser omitido, se utiliza como base el nombre de la escena provista.
*  `-pathtracer`: Habilita el modo de Path Tracing.
*  `-s <count>`: Cantidad de samples a utilizar en modo Path Tracing.
* `-time`: Agrega informacion de la imagen y render a la imagen final.
*  `-benchmark <N>`: Realiza el renderizado N veces y muestra un promedio de los tiempos.
* `-silent`: Desactiva los 'logs' de progreso mostrados durante el renderizado.
*  `-randname`: Agrega una componente aleatoria al final del nombre de archivo de la imagen producida.
*  `-nogamma`: Desactiva la correcion Gamma.
*  `-test`: Utiliza una escena de prueba.
