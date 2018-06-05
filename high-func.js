const find=(highfunc=>highfunc(highfunc))(
	func => (x,y,i=0) => (
		i >= x.length? null :
			x[i]==y?i:func(func)(x,y,i+1)
	)
);
let arr=[0,1,2,3,4,5]
console.log(find(arr,2))
