IDENTIFICATION DIVISION.
PROGRAM-ID.  SeqWrite.


ENVIRONMENT DIVISION.
INPUT-OUTPUT SECTION.
FILE-CONTROL.
    SELECT UserFile ASSIGN TO "USERS.DAT"
		ORGANIZATION IS INDEXED
        ACCESS MODE IS DYNAMIC
        RECORD KEY IS USER-TARJ
        FILE STATUS IS FSU.

    SELECT LoginFile ASSIGN TO  "LOGIN.DAT"
       ORGANIZATION IS INDEXED
       ACCESS MODE IS DYNAMIC
       RECORD KEY IS LOGIN-TARJ
       FILE STATUS IS FSL.

DATA DIVISION.
FILE SECTION.
FD USERFILE.
01 REG-USUARIO.
   02 USER-TARJ             PIC 9(10).
   02 USER-PIN              PIC 9(4).
   02 USER-DNI              PIC X(9).
   02 USER-NOM-APE          PIC X(30).
   02 USER-TFNO             PIC X(9).
   02 USER-DIRECCION        PIC X(25).
   02 USER-BLOQUEADA        PIC X.
   02 CUENTA-USUARIO        OCCURS 3 TIMES.
       03 USER-NUM-CUENTA       PIC X(24).
       03 USER-SALDO            PIC 9(9)V99.

FD LOGINFILE.
 01 REG-LOGIN.
   02 LOGIN-TARJ             PIC 9(10).
   02 LOGIN-NUM-INTENTOS     PIC 9.

WORKING-STORAGE SECTION.
01  FSU     PIC X(2).
01  FSL     PIC X(2).
77 L                         PIC 999 VALUE 1.
77 M                         PIC 999 VALUE 1.
01 CUENTA-BUSCADA PIC X(24).

01 WS-REG-USUARIO.
   02 WS-USER-TARJ             PIC 9(10).
   02 WS-USER-PIN              PIC 9(4).
   02 WS-USER-DNI              PIC X(9).
   02 WS-USER-NOM-APE          PIC X(30).
   02 WS-USER-TFNO             PIC X(9).
   02 WS-USER-DIRECCION        PIC X(25).
   02 WS-USER-BLOQUEADA        PIC X.
   02 WS-CUENTA-USUARIO OCCURS 3 TIMES.
       03 WS-USER-NUM-CUENTA       PIC X(24).
       03 WS-USER-SALDO            PIC 9(9)V99.

 01 WS-REG-LOGIN.
   02 WS-LOGIN-TARJ             PIC 9(10).
   02 WS-LOGIN-NUM-INTENTOS     PIC 9.


PROCEDURE DIVISION.
*>      PERFORM READ-LOGINFILE.
*>      PERFORM READ-USERSFILE.
        PERFORM TRANSFERIR-DINERO-CUENTA-DESTINO THRU FIN-TRANSFERIR-DINERO.
      STOP RUN.      




READ-LOGINFILE.
    MOVE 1234567890 TO LOGIN-TARJ

    OPEN INPUT LoginFile.
      READ LoginFile RECORD INTO WS-REG-LOGIN
        KEY IS LOGIN-TARJ
        INVALID KEY DISPLAY LOGIN-TARJ
        NOT INVALID KEY DISPLAY WS-REG-LOGIN
      END-READ.
    CLOSE LoginFile.
     


READ-USERSFILE.
    MOVE 1234567890 TO USER-TARJ
       

    OPEN INPUT UserFile.
      READ UserFile RECORD INTO WS-REG-USUARIO
        KEY IS USER-TARJ
        INVALID KEY DISPLAY USER-TARJ
        NOT INVALID KEY DISPLAY WS-USER-NUM-CUENTA(1)
      END-READ.
    CLOSE UserFile.

     STOP RUN.
       
TRANSFERIR-DINERO-CUENTA-DESTINO.
    OPEN I-O USERFILE.
        
  INICIO-OBTENER-CUENTAS.
    READ USERFILE NEXT RECORD INTO WS-REG-USUARIO
             AT END GO TO FIN-OBTENER-CUENTAS.
       MOVE 1 TO M.
       
       PERFORM BUSCAR-CUENTA UNTIL M = 4.
       
   GO TO INICIO-OBTENER-CUENTAS.
  
  
  FIN-OBTENER-CUENTAS.
        CLOSE USERFILE.
  FIN-TRANSFERIR-DINERO.



BUSCAR-CUENTA.
       IF WS-USER-NUM-CUENTA(M) = "ES3232323232323232323232"  
           COMPUTE WS-USER-SALDO(M) = WS-USER-SALDO(M) + 1
           DISPLAY WS-USER-SALDO(M)
           REWRITE REG-USUARIO.
       ADD 1 TO M.
       