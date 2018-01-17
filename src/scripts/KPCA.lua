KPCA = {}

local GAMMA = 0.00004
local TARGET_VARIANCE = 0.99

local function rbf(a, b) return math.exp(-GAMMA * torch.dist(a, b)^2) end--rbf()

function KPCA.computeEigens(data)
	local k = torch.Tensor(data:size(1), data:size(1))
	local fracIdentity = 1 / data:size(1)
	local eigenVals, eigenVecs
	local sum, varSum, count = 0, 0, 0
	
	for i = 1, data:size(1) do				--Compute lower triangle
		for j = 1, i do
			k[i][j] = rbf(data[i], data[j])
		end
	end
	for i = 1, data:size(1) do				--Symmetric -> reflect lower triangle to upper
		for j = 1, i - 1 do
			k[j][i] = k[i][j]
		end
	end
	data = nil								--Large memory usage, make sure it is cleaned
	collectgarbage()
											--Center the kernel
	k = k - 2*fracIdentity*k + fracIdentity*k*fracIdentity
	eigenVals, eigenVecs =  torch.symeig(k)
	k = nil
	collectgarbage()
	
	sum = torch.sum(eigenVals)
	
	for i = eigenVals:size(1), 1, -1 do		--Return enough components to capture the target variance
		varSum = varSum + eigenVals[i] / sum
		if varSum > TARGET_VARIANCE then
			count = eigenVals:size(1) - i
			break
		end
	end
	print(count.." components selected.")
	
	return eigenVals:narrow(1, 1, count), eigenVecs:narrow(1, 1, count)
end--computeEigens()

function KPCA.project(x, trainData, eigenVals, eigenVecs)
	local k = torch.Tensor(trainData:size(1))
	
	for i = 1, trainData:size(1) do
		k[i] = rbf(x, trainData[i])
	end
	
	print(eigenVals:size())
	print(k:size())
	return eigenVals * k
end--project()

return KPCA