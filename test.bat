javac -cp game_engine.jar SamplePlayer.java

java -Xmx2G -jar game_engine.jar 10 game.quoridor.QuoridorGame %RANDOM% 5000 game.quoridor.players.BlockRandomPlayer SamplePlayer
pause
java -Xmx2G -jar game_engine.jar 10 game.quoridor.QuoridorGame %RANDOM% 5000 SamplePlayer game.quoridor.players.BlockRandomPlayer
pause
java -Xmx2G -jar game_engine.jar 10 game.quoridor.QuoridorGame %RANDOM% 5000 game.quoridor.players.BlockRandomPlayer SamplePlayer
pause
java -Xmx2G -jar game_engine.jar 10 game.quoridor.QuoridorGame %RANDOM% 5000 SamplePlayer game.quoridor.players.BlockRandomPlayer
pause
java -Xmx2G -jar game_engine.jar 10 game.quoridor.QuoridorGame %RANDOM% 5000 game.quoridor.players.BlockRandomPlayer SamplePlayer
pause

