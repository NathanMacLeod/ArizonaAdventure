# Arizona Adventure

This is a game I programmed over the summer that originated as an in-joke between me and a friend who likes Arizona iced tea a little too much. 
I had a lot of fun making it and am pretty happy with how it came out.

I'm pretty happy with how game came out code structure wise. This was my first time implementing sound in java. In order to handle playing all the sounds,
I created a static class for the purpose of managing sounds, outside classes can request the sound manager to play a sound by giving the file path to the sound file, and
it will start a new thread to load the sound file and play it. Some sounds like the laser sound are pretty long and require early termination, so for this purpose
the sound manager will return an ID correlated to the sound. Enteties can then request to sound manager to terminate the sound early by passing it in this ID. The system
works for the most part, although it will occasionally get bogged down and pause sounds for a few seconds. I used to have a sound for bullet hits, but removed it
as their frequency caused this issue a lot more, and while it happens less now it still happens occasionally as you can hear at a few points in the video.

The game has a really simple save file system, that just saves to a text file.

I made almost all the art for the game by photoshiping images I found online using Paint.net, although my friend Devon is reponsible for a few sprites.
The sound assets were all found online, I included all the artists and sounds used in the credits at the end of the game.

# Gameplay Video

You can click the link below to view me playing through the game.

[![Gameplay](https://img.youtube.com/vi/b1m1dlqlCGo/0.jpg)](https://www.youtube.com/watch?v=b1m1dlqlCGo)

Here are some notable parts in the video:

[First Boss (3:30)](https://youtu.be/b1m1dlqlCGo?t=206)

[Second Boss (10:12)](https://youtu.be/b1m1dlqlCGo?t=607)

[Final Boss (17:10)](https://youtu.be/b1m1dlqlCGo?t=1025)
