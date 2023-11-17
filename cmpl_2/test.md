**文法测试用例**

```console
5 S
S->QT
T->+QT|$
Q->VR
R->*VR|$
V->(S)|i
```

**输入串测试用例**

```console
(i+i)*i#
```

**运行结果**
```md
分析过程：
0	[#, S]              	       (i+i)*i#	          
1	[#, T, Q]           	       (i+i)*i#	     S->QT
2	[#, T, R, V]        	       (i+i)*i#	     Q->VR
3	[#, T, R, ), S, (]  	       (i+i)*i#	    V->(S)
4	[#, T, R, ), S]     	        i+i)*i#	          
5	[#, T, R, ), T, Q]  	        i+i)*i#	     S->QT
6	[#, T, R, ), T, R, V]	        i+i)*i#	     Q->VR
7	[#, T, R, ), T, R, i]	        i+i)*i#	      V->i
8	[#, T, R, ), T, R]  	         +i)*i#	          
9	[#, T, R, ), T]     	         +i)*i#	      R->$
10	[#, T, R, ), T, Q, +]	         +i)*i#	    T->+QT
11	[#, T, R, ), T, Q]  	          i)*i#	          
12	[#, T, R, ), T, R, V]	          i)*i#	     Q->VR
13	[#, T, R, ), T, R, i]	          i)*i#	      V->i
14	[#, T, R, ), T, R]  	           )*i#	          
15	[#, T, R, ), T]     	           )*i#	      R->$
16	[#, T, R, )]        	           )*i#	      T->$
17	[#, T, R]           	            *i#	          
18	[#, T, R, V, *]     	            *i#	    R->*VR
19	[#, T, R, V]        	             i#	          
20	[#, T, R, i]        	             i#	      V->i
21	[#, T, R]           	              #	          
22	[#, T]              	              #	      R->$
23	[#]                 	              #	      T->$
24	[]                  	               	          
句子符合文法规则
文法为：
Q->VR
R->*VR|$
S->QT
T->+QT|$
V->(S)|i
first集为：
Q=[(, i]
R=[$, *]
S=[(, i]
T=[$, +]
V=[(, i]
follow集为：
Q=[#, ), +]
R=[#, ), +]
S=[#, )]
T=[#, )]
V=[#, ), *, +]
预测分析表为：
 	|#        |(        |)        |i        |*        |+        |
-----------------------------------------------------------------
Q	|Q->null  |Q->VR    |Q->null  |Q->VR    |Q->null  |Q->null  |
R	|R->$     |R->null  |R->$     |R->null  |R->*VR   |R->$     |
S	|S->null  |S->QT    |S->null  |S->QT    |S->null  |S->null  |
T	|T->$     |T->null  |T->$     |T->null  |T->null  |T->+QT   |
V	|V->null  |V->(S)   |V->null  |V->i     |V->null  |V->null  |

```
