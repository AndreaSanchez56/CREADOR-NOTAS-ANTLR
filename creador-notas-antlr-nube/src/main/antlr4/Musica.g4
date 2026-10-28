grammar Musica;

@header { package com.musica.parser; }

partitura  : instruccion* EOF ;
instruccion: tempo | instrumento | nota | silencio ;
tempo       : TEMPO ENTERO PUNTO_COMA ;
instrumento : INSTRUMENTO ENTERO PUNTO_COMA ;
nota        : NOTA? altura alteracion? ENTERO duracion? PUNTO_COMA ;
silencio    : SILENCIO duracion PUNTO_COMA ;
altura      : DO | RE | MI | FA | SOL | LA | SI | C | D | E | F | G | A | B ;
alteracion  : SOSTENIDO | BEMOL ;
duracion    : REDONDA | BLANCA | NEGRA | CORCHEA | SEMICORCHEA ;

TEMPO: 'TEMPO';
INSTRUMENTO: 'INSTRUMENTO';
NOTA: 'NOTA';
SILENCIO: 'SILENCIO';
DO: 'DO'; RE: 'RE'; MI: 'MI'; FA: 'FA'; SOL: 'SOL'; LA: 'LA'; SI: 'SI';
C: 'C'; D: 'D'; E: 'E'; F: 'F'; G: 'G'; A: 'A'; B: 'B';
SOSTENIDO: '#'; BEMOL: 'b';
REDONDA: 'REDONDA'; BLANCA: 'BLANCA'; NEGRA: 'NEGRA'; CORCHEA: 'CORCHEA'; SEMICORCHEA: 'SEMICORCHEA';
PUNTO_COMA: ';';
ENTERO: [0-9]+;
COMENTARIO: '//' ~[\r\n]* -> skip;
ESPACIO: [ \t\r\n]+ -> skip;
