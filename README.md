Filtrador Positronico
======

## 

------

## 

------

## Tareas pendientes

* Extraer metodos de filtrado en clases distintas
* Añadir a esas clases como atributos las imagenes que hayan procesado, para asi, en caso de que no se hayan modificado los parametros no se tenga que recalcular ese paso
* Usar herencia para poder mantener todos los pasos que se desean aplicar a una imagen en un array u otra estructura y asi poder añadir tantos filtros como se deseen en el orden que se deseen 
* Añadir seleccion de imagenes a traves de explorador de archivos
* Añadir interfaz grafica con sliders para poder seleccionar los parametros
* Añadir visualizacion de las imagenes en sus distintos pasos


------

## Resultados

------

| Imagen        | variance      | variance_scalar   |  radius   | threshold | p     | phi   |
| ------------- |:-------------:| :-------------:   | :-----:   | :------:  |:-----:|:----: |
| Libertad        | 0.6           |1.6                |10         | 66.3      |  16    |  0.01    |
| Nymeria         | 0.6           |   1.6             |   10      |   66.3    |  16    |  0.01   |
| Montaña       | 0.6           |    1.6            |   10      |   66.3    |  16    |  0.01    |
| Montaña2       | 0.6           |    1.6            |   10      |   200    |  21    |  0.04    |

* ### Libertad

<kbd><img src="LibertadG2.jpg" title="Imagen original" width="25%" height="25%"></kbd><kbd><img src="LibertadDoG.jpg" title="Imagen con DoG aplicada" width="25%" height="25%"></kbd>

* ### Nymeria
<kbd><img src="NymeriaG2.jpg" title="Imagen original" width="25%" height="25%"></kbd><kbd><img src="NymeriaDoG.jpg" title="Imagen con DoG aplicada" width="25%" height="25%"></kbd>

* ### Montaña
<kbd><img src="MontañaG2.jpg" title="Imagen original" width="30%" height="30%"></kbd><kbd><img src="MontañaDoG.jpg" title="Imagen con DoG aplicada" width="30%" height="30%"></kbd>
* ### Montaña2
<kbd><img src="MontañaG2.jpg" title="Imagen original" width="30%" height="30%"></kbd><kbd><img src="Montaña2DoG.jpg" title="Imagen con DoG aplicada" width="30%" height="30%"></kbd>
 


## Referencias

------

* [**XDoG: An eXtended difference-of-Gaussians compendium
including advanced image stylization**](https://users.cs.northwestern.edu/~sco590/winnemoeller-cag2012.pdf)  

* [**Video explicactivo de las DoG**](https://www.youtube.com/watch?v=5EuYKEvugLU&ab_channel=Acerola)


