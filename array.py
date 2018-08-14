import time


def multi(a,b):
    c=[[0,0],[0,0]]
    for i in range(2):
        for j in range(2):
            for k in range(2):
                c[i][j]=c[i][j]+a[i][k]*b[k][j]
    print(c)
    return c


def matrix(n):
    base=[[1,1],[1,0]]
    ans=[[4,3],[0,1]]
    while n:
        if n&1:
            ans=multi(ans,base)
        base=multi(base,base)
        n>>=1
    # for i in range(2):
    #     print(ans[i])
    return ans[0][1]

def normal(n):
    a,b,c=0,1,1
    while n:
        a,b,c=b,c,b+c
        n-=1
    return a

n=int(input(">>>"))

start=time.time()
print("Matrix:",matrix(n))
print("use:",time.time()-start)
